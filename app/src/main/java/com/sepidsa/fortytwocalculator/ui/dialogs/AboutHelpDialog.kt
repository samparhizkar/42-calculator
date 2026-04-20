package com.sepidsa.fortytwocalculator.ui.dialogs

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sepidsa.fortytwocalculator.R

// ─────────────────────────────────────────────────────────────────────────────
// About Dialog
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun AboutDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(stringResource(R.string.farsi_about_42_calc))
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ── Company section ──
                Image(
                    painter = painterResource(R.drawable.sepidsa_icon_1440),
                    contentDescription = "Sepidsa",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    onClick = { openUrl(context, "http://blog.sepidsa.com") }
                ) {
                    Text(
                        text = stringResource(R.string.about_string),
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                // ── Company social links ──
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    SocialIconButton(
                        icon = { Icon(Icons.Default.Language, contentDescription = "Website") },
                        onClick = { openUrl(context, "http://blog.sepidsa.com") }
                    )
                    SocialIconButton(
                        icon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                        onClick = { sendEmail(context, "info@sepidsa.com") }
                    )
                    SocialIconButton(
                        icon = {
                            Icon(
                                painterResource(R.drawable.ic_facebook_box_grey600_24dp),
                                contentDescription = "Facebook",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        onClick = { openUrl(context, "http://facebook.com/teamsepidsa") }
                    )
                    SocialIconButton(
                        icon = {
                            Icon(
                                painterResource(R.drawable.ic_instagram_grey600_24dp),
                                contentDescription = "Instagram",
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        onClick = { openUrl(context, "http://instagram.com/teamsepidsa") }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(16.dp))

                // ── Developers section ──
                Text(
                    text = "Developers",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DeveloperCard(
                        name = "فرشید",
                        avatarRes = R.drawable.farshid_avatar,
                        onEmailClick = { sendEmail(context, "farshid@sepidsa.com") },
                        onLinkedInClick = {
                            openUrl(context, "https://ir.linkedin.com/pub/farshid-imanipour/97/74a/a93")
                        },
                        onInstagramClick = {
                            openUrl(context, "http://instagram.com/f4rsh")
                        }
                    )
                    DeveloperCard(
                        name = "احسان",
                        avatarRes = R.drawable.ehsan_avatar,
                        onEmailClick = { sendEmail(context, "ehsan@sepidsa.com") },
                        onLinkedInClick = {
                            openUrl(context, "https://www.linkedin.com/pub/ehsan-parhizkar/97/843/b79")
                        },
                        onInstagramClick = {
                            openUrl(context, "http://instagram.com/EHS4NPAR")
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "© 2024-2026 Sepidsa. All rights reserved.",
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.farsi_ok))
            }
        },
        shape = MaterialTheme.shapes.extraLarge
    )
}

@Composable
private fun DeveloperCard(
    name: String,
    avatarRes: Int,
    onEmailClick: () -> Unit,
    onLinkedInClick: () -> Unit,
    onInstagramClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(avatarRes),
            contentDescription = name,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Text(
            text = name,
            style = MaterialTheme.typography.titleSmall
        )

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            SocialIconButton(
                icon = { Icon(Icons.Default.Email, contentDescription = "Email", modifier = Modifier.size(18.dp)) },
                onClick = onEmailClick
            )
            SocialIconButton(
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_linkedin_white_24dp),
                        contentDescription = "LinkedIn",
                        modifier = Modifier.size(18.dp)
                    )
                },
                onClick = onLinkedInClick
            )
            SocialIconButton(
                icon = {
                    Icon(
                        painterResource(R.drawable.ic_instagram_white_24dp),
                        contentDescription = "Instagram",
                        modifier = Modifier.size(18.dp)
                    )
                },
                onClick = onInstagramClick
            )
        }
    }
}

@Composable
private fun SocialIconButton(
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(40.dp)
    ) {
        icon()
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Help Dialog
// ─────────────────────────────────────────────────────────────────────────────

@Composable
fun HelpDialog(onDismiss: () -> Unit) {
    val topics = stringArrayResource(R.array.help_topics)
    val subTopics = stringArrayResource(R.array.help_sub_topics)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDismiss) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("راهنما") // "Help" in Persian, matching the original activity_help.xml title
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                topics.forEachIndexed { index, topic ->
                    val subTopic = subTopics.getOrElse(index) { "" }
                    ExpandableHelpItem(
                        title = topic,
                        content = subTopic
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.farsi_ok))
            }
        },
        shape = MaterialTheme.shapes.extraLarge
    )
}

@Composable
private fun ExpandableHelpItem(title: String, content: String) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Start
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Expandable content
            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Utility functions
// ─────────────────────────────────────────────────────────────────────────────

private fun openUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(intent)
}

private fun sendEmail(context: Context, recipient: String) {
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:$recipient")
    }
    context.startActivity(Intent.createChooser(intent, context.getString(R.string.farsi_choose_email_app)))
}
